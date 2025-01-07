export interface SystemCpu{

    name: string;
    description: string;
    measurements: [
        {
            statistic: string,
            value: number
        }
    ];
    availableTags: any[

    ]
    
}