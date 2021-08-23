export const environment = {
  production: false,
  api: {
    atm: {
      search: "http://localhost/api/atms/search/:query/:page/:size"
    },
    auth: {
      login: "http://localhost/api/auth/login"
    }
  }
};
