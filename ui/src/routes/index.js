import React, { Suspense } from "react";
import Nav from "../components/Nav";
import DataViewer from "../components/DataViewer";
import Main from "../components/Main";

function Index() {
  return (
    <>
      <Nav />
      <Main>
        <Suspense fallback={"loading..."}>
          <DataViewer />
        </Suspense>
      </Main>
    </>
  );
}

export default Index;
