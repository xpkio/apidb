import { transformKey, caseMethods } from "./case";

export const createProxy = (object, callback, path = [""]) => {
  if (typeof path === "string") path = [path];

  const call = (...args) => {
    const method = {
      post: "POST",
      get: "GET",
      put: "PUT",
      patch: "PATCH",
      delete: "DELETE"
    }[path[path.length - 1]];

    if (!method) throw new Error(`No method selected for ${path.join("/")}`);

    const urlBase = path.slice(0, -1).join("/");

    return callback(method, urlBase, ...args);
  };

  const proxy = new Proxy(call, {
    get: (_, prop) => {
      if (object[prop]) return object[prop];
      prop = transformKey(prop, caseMethods.snake);
      return createProxy(object, callback, [...path, prop]);
    }
  });

  return proxy;
};
