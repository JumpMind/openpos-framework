import { OpenposBarcodeType } from './openpos-barcode-type.enum';
import { InfineaBarcodeType } from './infinea-barcode-type.enum';

export class InfineaBarcodeUtils {

    private static infineaToOpenposTypeMap = new Map([
        [InfineaBarcodeType.CODE128, OpenposBarcodeType.CODE128],
        [InfineaBarcodeType.EAN13, OpenposBarcodeType.EAN13],
        [InfineaBarcodeType.EAN13_2, OpenposBarcodeType.EAN13_2],
        [InfineaBarcodeType.EAN13_5, OpenposBarcodeType.EAN13_5],
        [InfineaBarcodeType.UPCA, OpenposBarcodeType.UPCA],
        [InfineaBarcodeType.UPCA_2, OpenposBarcodeType.UPCA_2],
        [InfineaBarcodeType.UPCA_5, OpenposBarcodeType.UPCA_5],
        [InfineaBarcodeType.UPCE, OpenposBarcodeType.UPCE],
        [InfineaBarcodeType.UPCE_2, OpenposBarcodeType.UPCE_2],
        [InfineaBarcodeType.UPCE_5, OpenposBarcodeType.UPCE_5]
    ]);

    static convertToOpenposType( type: InfineaBarcodeType ): OpenposBarcodeType {
        if ( InfineaBarcodeUtils.infineaToOpenposTypeMap.has(type)) {
            return InfineaBarcodeUtils.infineaToOpenposTypeMap.get(type);
        }

        return null;
    }
}
