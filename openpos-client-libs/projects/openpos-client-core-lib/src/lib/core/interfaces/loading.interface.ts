export interface ILoading {
    type: string;
    queue?: boolean;
    cancel?: boolean;
    title?: string;
    message?: string;
}
