import { ScanDataType } from '../image-scanner';
import { Barcode } from './types/barcode';

export class ScanditBarcodeUtils {

    private static scanditToOpenPosMap = new Map<Barcode.Symbology, ScanDataType>([
        [Barcode.Symbology.Aztec, 'AZTEK'],
        [Barcode.Symbology.Codabar, 'CODABAR'],
        [Barcode.Symbology.Code11, 'CODE11'],
        [Barcode.Symbology.Code25, 'CODE25_NI2OF5'],
        [Barcode.Symbology.Code39, 'CODE39'],
        [Barcode.Symbology.Code93, 'CODE93'],
        [Barcode.Symbology.Code128, 'CODE128'],
        [Barcode.Symbology.EAN8, 'EAN8'],
        [Barcode.Symbology.GS1Databar, 'GS1DATABAR'],
        [Barcode.Symbology.MaxiCode, 'MAXICODE'],
        [Barcode.Symbology.MSIPlessey, 'MSI_PLESSEY'],
        [Barcode.Symbology.PDF417, 'PDF417'],
        [Barcode.Symbology.QR, 'QRCODE'],
        [Barcode.Symbology.RM4SCC, 'RM4SCC'],
        [Barcode.Symbology.EAN13UPCA, 'UPCA'],
        [Barcode.Symbology.UPCE, 'UPCE'],
        [Barcode.Symbology.InterleavedTwoOfFive, 'ITF'],
        [Barcode.Symbology.ITF, 'ITF'],
    ]);


    static convertToOpenposType( type: Barcode.Symbology, data: string ): ScanDataType {
        if(type === Barcode.Symbology.EAN13UPCA && data[0] !== '0'){
            return 'EAN13';
        }

        if( this.scanditToOpenPosMap.has(type) ){
            return this.scanditToOpenPosMap.get(type);
        }

        throw Error(`Barcode type ${type} not supported`);
    }

    static convertFromOpenposType(type: ScanDataType): Barcode.Symbology {
        let matches  = Array.from(this.scanditToOpenPosMap.entries()).filter(value => value[1] === type);

        if (matches.length > 0) {
            return matches[0][0];
        }

        throw Error(`Barcode type ${type} not supported`);
    }
}
