export interface Atm {
  id: string | null;
  distance: number;
  type: string;
  address: Address;
}

export interface Address {
  street: string;
  housenumber: string;
  postalcode: string;
  city: string;
  geoLocation: GeoLocation;
}

export interface GeoLocation {
  lat: string;
  lng: string;
}

