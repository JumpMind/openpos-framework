export interface IFormatter {
    allowKey( key: string, newValue: string ): boolean;
    formatValue( value: string | Date ): string;
    unFormatValue( value: string ): string;
}
