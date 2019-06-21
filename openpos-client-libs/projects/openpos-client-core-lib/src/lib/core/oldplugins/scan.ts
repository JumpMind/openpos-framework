export class Scan {
    constructor(public value: string, public format: string, public cancelled: boolean = false, public source?: string) {
    }
}
