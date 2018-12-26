import { createResource } from "./cache";
import { createProxy } from "./proxy";
import { transformKeys, caseMethods } from "./case";

export default (urlBase, headers) => {
  async function resolve(path, other = {}) {
    const res = await fetch(urlBase + path, {
      headers: {
        ...headers
      },
      ...other,
      body: other.body ? JSON.stringify(other.body) : undefined
    });

    if (!res.ok) throw new Error(await res.text());

    const body = transformKeys(await res.json(), caseMethods.camel);

    let result = null;

    if (Array.isArray(body.data)) {
      const collection = Object.assign(body.data, body.meta);

      bindLinks(collection);
      result = collection;
    } else {
      bindLinks(body);
      result = body;
    }

    return result;
  }

  const resource = createResource(resolve);

  // Very un-pure
  function bindLinks(object) {
    const { _links: links } = object;

    for (let value of Object.values(object)) {
      if (value && typeof value === "object") {
        bindLinks(value);
      }
    }

    if (!links) return;

    for (let [key, url] of Object.entries(links)) {
      Object.defineProperty(object, key, {
        get() {
          const data = resource.read(url);
          return data.body;
        },
        enumerable: false,
        configurable: false
      });
    }
  }

  const proxy = createProxy(resource, (method, path, params = {}, other) => {
    const query =
      Object.keys(params).length === 0
        ? ""
        : `${path.includes("?") ? "&" : "?"}${Object.entries(params)
            .map(v => v.map(encodeURIComponent))
            .map(a => a.join("="))
            .join("&")}`;

    const url = `${path}${query}`;

    if (method !== "GET") return resolve(url, { method, ...other });
    const data = resource.read(url);

    return data.body;
  });

  return proxy;
};
