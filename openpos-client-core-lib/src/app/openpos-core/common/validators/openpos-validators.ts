import { FormControl, FormGroup } from '@angular/forms';

export class OpenPosValidators {

    static PhoneUS(c: FormControl) {
        const regex = /^[2-9][0-8][0-9][2-9][0-9]{6}$/;

        if (c.value) {
            return regex.test(c.value) ? null : {
                'phoneUS': {
                    valid: false
                }
            };
        } else {
            return null;
        }
    }

    static PhoneCA(c: FormControl) {
        const regex = /^[0-9]{10}$/;

        if (c.value) {
            return regex.test(c.value) ? null : {
                'phone': {
                    valid: false
                }
            };
        } else {
            return null;
        }
    }

    static GiftCode(c: FormControl) {
        return c.value && c.value.length > 2 ? null : {
            'minlength': {
                valid: false
            }
        };
    }

    static RequireAtleastOne(form: FormGroup) {
        for (const name of Object.getOwnPropertyNames(form.value)) {
            const value = form.value[name];
            if (value !== '') {
                return null;
            }
        }
        return {
            'requireAtleastOne': {
                valid: false
            }
        };
    }

    static DateMMDDYYYY(c: FormControl) {
        if (c.value && (typeof c.value  === 'string')) {
            const dateParts = c.value.split('/');
            if (dateParts.length !== 3) {
                return {
                    'date': {
                        valid: false
                    }
                };
            } else {
                const month = Number(dateParts[0]);
                const dayOfMonth = Number(dateParts[1]);
                const year = Number(dateParts[2]);
                const date = new Date(year, month - 1, dayOfMonth);
                // console.log(`Checking validity of entered date '${month}/${dayOfMonth}/${year}' ` +
                // `vs. parsed date '${date.getMonth() + 1}/${date.getDate()}/${date.getFullYear()}'`);
                if (month === date.getMonth() + 1 && dayOfMonth === date.getDate() && year === date.getFullYear()) {
                    // console.log('Date is valid');
                    return null;
                } else {
                    // console.log('Date is not valid');
                    return {
                        'date': {
                            valid: false
                        }
                    };
                }
            }
        }
    }
}
