import { ScanDataType } from '../scanner';
import { InfineaBarcodeType } from './infinea-barcode-type.enum';

export class InfineaBarcodeUtils {

    private static infineaToOpenposTypeMap = new Map<InfineaBarcodeType, ScanDataType>([
        [InfineaBarcodeType.AZTEK, 'AZTEK'],
        [InfineaBarcodeType.CCA, 'CCA'],
        [InfineaBarcodeType.CCB, 'CCB'],
        [InfineaBarcodeType.CCC, 'CCC'],
        [InfineaBarcodeType.CODABAR, 'CODABAR'],
        [InfineaBarcodeType.CODABAR_ABC, 'CODABAR_ABC'],
        [InfineaBarcodeType.CODABAR_CX, 'CODABAR_CX'],
        [InfineaBarcodeType.CODE11, 'CODE11'],
        [InfineaBarcodeType.CODE128, 'CODE128'],
        [InfineaBarcodeType.CODE25_I2OF5, 'CODE25_I2OF5'],
        [InfineaBarcodeType.CODE25_NI2OF5, 'CODE25_NI2OF5'],
        [InfineaBarcodeType.CODE39, 'CODE39'],
        [InfineaBarcodeType.CODE39_FULL, 'CODE39_FULL'],
        [InfineaBarcodeType.CODE93, 'CODE93'],
        [InfineaBarcodeType.CPCBINARY, 'CPCBINARY'],
        [InfineaBarcodeType.DATAMATRIX, 'DATAMATRIX'],
        [InfineaBarcodeType.DUN14, 'DUN14'],
        [InfineaBarcodeType.EAN128, 'EAN128'],
        [InfineaBarcodeType.EAN13, 'EAN13'],
        [InfineaBarcodeType.EAN13_2, 'EAN13_2'],
        [InfineaBarcodeType.EAN13_5, 'EAN13_5'],
        [InfineaBarcodeType.EAN2, 'EAN2'],
        [InfineaBarcodeType.EAN5, 'EAN5'],
        [InfineaBarcodeType.EAN8, 'EAN8'],
        [InfineaBarcodeType.EAN8_2, 'EAN8_2'],
        [InfineaBarcodeType.EAN8_5, 'EAN8_5'],
        [InfineaBarcodeType.GS1DATABAR, 'GS1DATABAR'],
        [InfineaBarcodeType.IATA, 'IATA'],
        [InfineaBarcodeType.INTELLIGENT_MAIL, 'INTELLIGENT_MAIL'],
        [InfineaBarcodeType.ITA_PHARMA, 'ITA_PHARMA'],
        [InfineaBarcodeType.ITF14, 'ITF14'],
        [InfineaBarcodeType.KOREAN_POSTAL, 'KOREAN_POSTAL'],
        [InfineaBarcodeType.LATENT_IMAGE, 'LATENT_IMAGE'],
        [InfineaBarcodeType.MATRIX_2OF5, 'MATRIX_2OF5'],
        [InfineaBarcodeType.MAXICODE, 'MAXICODE'],
        [InfineaBarcodeType.MICROPDF417, 'MICROPDF417'],
        [InfineaBarcodeType.MSI_PLESSEY, 'MSI_PLESSEY'],
        [InfineaBarcodeType.PDF417, 'PDF417'],
        [InfineaBarcodeType.PHARMACODE, 'PHARMACODE'],
        [InfineaBarcodeType.PLANET, 'PLANET'],
        [InfineaBarcodeType.POSTBAR, 'POSTBAR'],
        [InfineaBarcodeType.QRCODE, 'QRCODE'],
        [InfineaBarcodeType.RM4SCC, 'RM4SCC'],
        [InfineaBarcodeType.SCODE, 'SCODE'],
        [InfineaBarcodeType.TELEPEN, 'TELEPEN'],
        [InfineaBarcodeType.UK_PLESSEY, 'UK_PLESSEY'],
        [InfineaBarcodeType.UPCA, 'UPCA'],
        [InfineaBarcodeType.UPCA_2, 'UPCA_2'],
        [InfineaBarcodeType.UPCA_5, 'UPCA_5'],
        [InfineaBarcodeType.UPCE, 'UPCE'],
        [InfineaBarcodeType.UPCE_2, 'UPCE_2'],
        [InfineaBarcodeType.UPCE_5, 'UPCE_5']
    ]);

    static convertToOpenposType( type: InfineaBarcodeType ): ScanDataType {
        if (InfineaBarcodeUtils.infineaToOpenposTypeMap.has(type)) {
            return InfineaBarcodeUtils.infineaToOpenposTypeMap.get(type);
        }

        return null;
    }
}
