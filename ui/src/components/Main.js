import React from "react";
import styled from "styled-components";

function Main({ children }) {
  return <Container>{children}</Container>;
}

export default Main;

const Container = styled.div`
  padding: 40px;
`;
