export interface Enviroments {
    production: boolean;
    serverUrl: string;
}


export const environment = {
    production: false,
    serverUrl: 'http://localhost:9000/actuator'
};
