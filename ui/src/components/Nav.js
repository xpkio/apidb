import React from "react";
import styled from "styled-components";

function Nav() {
  return <Container>ApiDB</Container>;
}

export default Nav;

const Container = styled.nav`
  height: 50px;
  line-height: 50px;
  background: #202020;
  color: white;
  padding: 0 20px;
`;
