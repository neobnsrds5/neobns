export interface SystemHealth {

    status: string;
    components: {
        databaseService: {
            status: string;
            details: {
                DatabaseService: string;
            }
        },
        diskSpace: {
            status: string,
            details: {
                total: number,
                free: number | string,
                threshold: number,
                path: string,
                exists: boolean
            }
        },
        loggerService: {
            status: string,
            details: {
                LoggerService: string;
            }
        },
        ping: {
            status: string;
        }
    };
}