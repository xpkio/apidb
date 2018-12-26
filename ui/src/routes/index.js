import React, { Suspense } from "react";
import Nav from "../components/Nav";
import DataViewer from "../components/DataViewer";
import Main from "../components/Main";
import Loader from "../components/Loader";

function Index() {
  return (
    <>
      <Nav />
      <Main>
        <Suspense fallback={<Loader />}>
          <DataViewer title={"Api Endpoints"} />
        </Suspense>
      </Main>
    </>
  );
}

export default Index;
