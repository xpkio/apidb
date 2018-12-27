import React from "react";
import styled from "styled-components";
import { THEME_COLOR } from "../colors";

function Nav() {
  return (
    <Container>
      <svg viewBox="0 0 675 50">
        <path d="M0.5,0.5 L0.5,49.5 L622.100505,49.5 C623.824412,49.5 625.477713,48.8151805 626.696699,47.5961941 L673.792893,0.5 L0.5,0.5 Z" />
      </svg>
      <div>
        <h1>ApiDb</h1>
      </div>
    </Container>
  );
}

export default Nav;

const Container = styled.nav`
  height: 50px;
  line-height: 48px;
  padding: 0 20px;
  display: inline-block;
  position: relative;
  color: white;
  svg {
    position: absolute;
    top: -1px;
    right: -50px;
    width: 675px;
    fill: ${THEME_COLOR};
    /* stroke: #f0f0f0; */
  }
  & > div {
    position: relative;
    z-index: 1;
    h1 {
      font-weight: bold;
    }
  }
`;
