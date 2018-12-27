import { useEffect, useState } from "react";

export function createResource(fn) {
  const cache = {};
  const promises = {};

  const load = id => {
    let promise = promises[id];

    if (!promise) {
      promise = promises[id] = fn(id)
        .then(body => {
          delete promises[id];
          cache[id] = { body, ids: findIds(body), subscribers: new Set() };
          return cache[id];
        })
        .catch(e => {
          console.error(e);
          // @TODO this purposely never resolves. :(
          return new Promise(r => {});
        });
    }

    return promise;
  };

  return {
    read(id) {
      const data = cache[id];

      const forceUpdate = useForceUpdate();
      useEffect(() => {
        if (!cache[id]) return;
        cache[id].subscribers.add(forceUpdate);
        return () => cache[id] && cache[id].subscribers.delete(forceUpdate);
      });

      if (!data) throw load(id);

      return data;
    },

    async invalidate(key) {
      const entry = cache[key];
      if (!entry) return;
      delete cache[key];

      if (entry.subscribers.size > 0) await load(key);
      return () => entry.subscribers.forEach(fn => fn());
    },
    async invalidateKey(key) {
      const update = await this.invalidate(key);
      update();
    },
    async invalidateId(id) {
      const fns = await Promise.all(
        Object.entries(cache)
          .filter(([_, entry]) => entry.ids.has(id))
          .map(([key]) => this.invalidate(key))
      );
      fns.forEach(fn => fn());
    },
    async invalidateAll(query) {
      const fns = await Promise.all(
        Object.entries(cache)
          .filter(([key]) => key.includes(query))
          .map(([key]) => this.invalidate(key))
      );
      fns.forEach(fn => fn());
    }
  };
}

const useForceUpdate = () => {
  const [bool, set] = useState(false);
  return () => set(!bool);
};

const findIds = obj => {
  const ids = [];

  for (let [key, value] of Object.entries(obj)) {
    if ((key === "id" || /[a-z]Id$/.test(key)) && value) {
      ids.push(value);
    } else if (value && typeof value === "object") {
      ids.push(...findIds(value));
    }
  }

  return new Set(ids);
};
