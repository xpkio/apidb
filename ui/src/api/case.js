export const caseMethods = {
  camel(word, index) {
    return index === 0 ? word : word[0].toUpperCase() + word.slice(1);
  },
  snake(word, index) {
    return index === 0 ? word : "_" + word;
  }
};

export const transformKey = (key, method) =>
  key
    .replace(/_/g, " ")
    .replace(/(\b|^|[a-z])([A-Z])/g, "$1 $2")
    .replace(/ +/g, " ")
    .trim()
    .toLowerCase()
    .split(" ")
    .reduce(
      (str, word, index) => str + method(word, index),
      key.startsWith("_") ? "_" : ""
    );

export const transformKeys = (obj, method) => {
  if (typeof obj !== "object") return obj;
  if (!obj) return obj;
  if (Array.isArray(obj)) return obj.map(item => transformKeys(item, method));

  return Object.keys(obj)
    .map(key => ({ key, value: transformKeys(obj[key], method) }))
    .map(({ key, value }) => ({
      value,
      key: transformKey(key, method)
    }))
    .reduce(
      (returned, { key, value }) => Object.assign(returned, { [key]: value }),
      {}
    );
};
