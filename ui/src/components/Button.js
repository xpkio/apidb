import styled from "styled-components";
import { darken, lighten } from "polished";
import { THEME_COLOR } from "../colors";

export default styled.button`
  display: inline-block;
  background: ${THEME_COLOR};
  color: white;
  border-radius: 3px;
  border: 0;
  font-size: 14px;
  line-height: 1.7em;
  padding: 0 20px;
  font-weight: 600;
  &:hover {
    background: ${lighten(0.1, THEME_COLOR)};
  }
  &:active {
    background: ${darken(0.1, THEME_COLOR)};
  }
`;
