export class SessionStorage{
    static get (key) {
        const v = sessionStorage.getItem(key);
        if (v && typeof(v) !== "undefined" && v !== "undefined") {
            return JSON.parse(v);
        }
    }

    static set(key, data) {
        sessionStorage.setItem(key, JSON.stringify(data));
    }
    static remove(key) {
        sessionStorage.removeItem(key);
    }

    static clearAll () {
        sessionStorage.clear();
    }
}

