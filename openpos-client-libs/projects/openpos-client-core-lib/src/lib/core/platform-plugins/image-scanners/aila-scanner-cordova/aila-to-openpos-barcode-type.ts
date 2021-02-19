import { AilaBarcodeType } from './aila-barcode-type.enum';
import { ScanDataType } from '../image-scanner';

export class AilaBarcodeUtils {

    static convertToOpenposType( type: AilaBarcodeType, length: number ): ScanDataType {
        switch ( type ) {
            case AilaBarcodeType.UPC_EAN:
                // This is terrible but as of now we don't know the difference between EAN and UPC
                switch (length) {
                    case 8:
                        return 'UPCE';
                    case 12:
                        return 'UPCA';
                    case 13:
                        return 'EAN13';
                    default:
                        return 'UPC_UNKNOWN';
                }
            case AilaBarcodeType.QR:
                return 'QRCODE';
            case AilaBarcodeType.Code128:
                return 'CODE128';
            case AilaBarcodeType.Code25_2of5:
                // this is a guess
                return 'CODE25_I2OF5';
            case AilaBarcodeType.PDF417:
                return 'PDF417';
            case AilaBarcodeType.DataBar:
                return 'GS1DATABAR';
            default:
                return null;
        }
    }
}
