export interface ErrorRespone {
    timestamp: string;
    status: number;
    message: string;
    details?: string[];
}