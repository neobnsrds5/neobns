export interface Enviroments {
    production: boolean;
    serverUrl: string;
}


export const environment = {
    production: false,
    serverUrl: 'http://localhost:8000/actuator'
};
