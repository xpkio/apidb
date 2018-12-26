import React, { useState, Suspense } from "react";
import styled from "styled-components";
import { humanize, underscore } from "inflection";
import { apidb, api } from "../api";
import Button from "./Button";
import Loader from "./Loader";

function DataViewer({ title, fetcher = () => apidb.get(), close }) {
  const [selectedItem, select] = useState(null);
  const data = fetcher();

  const getViewer = () => {
    if (Array.isArray(data)) {
      return (
        <TableViewer data={data} select={select} selectedItem={selectedItem} />
      );
    }

    // @TODO add other viewers like numbers and single row
    return null;
  };

  const viewer = getViewer();

  return (
    <>
      <Tile>
        <div>
          {Boolean(close) && <CloseButton onClick={close}>Close</CloseButton>}
          {Boolean(title) && <H2>{title}</H2>}
        </div>
        {viewer}
      </Tile>
      {Boolean(selectedItem) && (
        <Suspense fallback={<Loader />}>
          <DataViewer
            fetcher={() =>
              // @TODO needs another way know which schema is the one to pick from
              (selectedItem.path !== "/" ? api : apidb)[selectedItem.path].get()
            }
            close={() => select(null)}
            title={selectedItem.title}
          />
        </Suspense>
      )}
    </>
  );
}

function TableViewer({ data, select, selectedItem }) {
  //@TODO cant show a table without any meta data
  if (data.length === 0) return <div>No data</div>;

  if (selectedItem) data = data.filter(item => item.id === selectedItem.id);

  const headers = Object.keys(data[0]);

  return (
    <Table>
      <thead>
        <tr>
          {headers.map(header => (
            <th key={header}>{humanize(underscore(header))}</th>
          ))}
          <th>Links</th>
        </tr>
      </thead>
      <tbody>
        {data.map((row, index) => (
          <tr
            key={index}
            // tabIndex={0}
            // role="button"
            // onClick={() => select(row)}
          >
            {Object.values(row).map((column, index) => (
              <td key={index}>{column}</td>
            ))}
            <td>
              <Button onClick={() => select(row)}>View</Button>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
}

export default DataViewer;

const Table = styled.table`
  width: 100%;
  height: 100%;
  th,
  td {
    border-bottom: 1px solid #dadee4;
    padding: 0.6rem 0.4rem;
  }
  thead {
    th {
      border-bottom-width: 2px;
      text-align: left;
      font-weight: bold;
    }
  }
  tr:nth-child(even) {
    background: rgba(0, 0, 0, 0.025);
  }
`;

const H2 = styled.h2`
  font-size: 24px;
  margin-bottom: 1em;
`;

const Tile = styled.div`
  padding: 20px;
  border: 1px solid #f8f8f8;
  box-shadow: 0px 8px 32px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 1;
  margin-bottom: 40px;
  background: white;
  border-radius: 7px;
`;

const CloseButton = styled(Button)`
  float: right;
`;
