export const environment = {
  production: true,
  api: {
    atm: {
      search: "http://localhost/api/consumer/search/:query/:page/:size"
    },
    auth: {
      login: "http://localhost/api/auth/login"
    }
  }
};
