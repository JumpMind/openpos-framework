export interface DiscoveryResponse {
    success: boolean;
    message?: string;
    host?: string;
    port?: string;
    webServiceBaseUrl?: string;
    secureWebServiceBaseUrl?: string;
    webSocketBaseUrl?: string;
    secureWebSocketBaseUrl?: string;
}
