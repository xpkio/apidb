import createEndpoint from "./create-endpoint";

// Define your endpoints
export const apidb = createEndpoint("/api", {});

export const api = createEndpoint("/api", {
  "tenant-db-name": "steve"
});
