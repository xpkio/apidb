import styled, { keyframes } from "styled-components";
import { THEME_COLOR } from "../colors";

import React from "react";

const loaderKeyframes = keyframes`
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
`;

export const Spinner = styled.div`
  border-radius: 10000px;
  width: 50px;
  height: 50px;
  border: 4px solid transparent;
  border-left-color: ${THEME_COLOR};
  animation: 1s ${loaderKeyframes} linear infinite;
  margin: auto;
  margin-bottom: 20px;
`;

const Container = styled.div`
  position: relative;
  height: 100%;
  min-height: 400px;
  ${Spinner} {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
  }
`;

function Loader() {
  return (
    <Container>
      <Spinner />
    </Container>
  );
}

export default Loader;
