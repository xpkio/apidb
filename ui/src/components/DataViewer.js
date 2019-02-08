import React, { useState, Suspense } from "react";
import styled from "styled-components";
import { humanize, underscore } from "inflection";
import { apidb, api } from "../api";
import Button from "./Button";
import Loader from "./Loader";

function DataViewer({ title, fetcher = () => apidb.apis.get(), close }) {
  const [selection, select] = useState(null);
  const data = fetcher();

  const getViewer = () => {
    if (Array.isArray(data.results)) {
      return <TableViewer data={data} select={select} selection={selection} />;
    }

    // @TODO add other viewers like numbers and single row
    return null;
  };

  const viewer = getViewer();

  console.log(selection);

  return (
    <>
      <Tile>
        <div>
          {Boolean(close) && <CloseButton onClick={close}>Close</CloseButton>}
          {Boolean(title) && <H2>{title}</H2>}
        </div>
        {viewer}
      </Tile>
      {Boolean(selection) && (
        <Suspense fallback={<Loader />}>
          <DataViewer
            fetcher={() =>
              // @TODO needs another way know which schema is the one to pick from
              // @TODO ^^^^^ This is getting more important
              (selection.link.link.includes("/steve") ? api : apidb)[
                selection.link.link
              ].get()
            }
            close={() => select(null)}
            title={selection.item.title}
          />
        </Suspense>
      )}
    </>
  );
}

function TableViewer({ data, select, selection }) {
  // Build links for each row
  const rows = data.results.map(row => {
    return {
      ...row,
      _links: data.links
        .filter(link => link.type === "inline")
        .map(link => {
          return Object.entries(link)
            .map(([key, value]) => [
              key,
              typeof value !== "string"
                ? value
                : value.replace(
                    /\$([a-zA-Z$_][a-zA-Z0-9$_]+)/g,
                    (_, replaceKey) => {
                      return row[replaceKey] || null;
                    }
                  )
            ])
            .reduce((acc, [key, value]) => ({ ...acc, [key]: value }), {});
        })
    };
  });

  //@TODO cant show a table without any meta data
  if (rows.length === 0) return <div>No data</div>;

  if (selection) data = rows.filter(item => item.id === selection.item.id);

  const headers = Object.keys(rows[0]).filter(x => !x.startsWith("_"));

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
        {rows.map(({ _links: links, ...row }, index) => (
          <tr key={index}>
            {Object.values(row).map((column, index) => (
              <td key={index}>{column}</td>
            ))}
            <td>
              {links.map((link, index) => (
                <Button key={index} onClick={() => select({ item: row, link })}>
                  {link.name}
                </Button>
              ))}
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
