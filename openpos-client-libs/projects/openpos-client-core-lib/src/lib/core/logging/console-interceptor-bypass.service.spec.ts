import { ConsoleInterceptorBypassService } from './console-interceptor-bypass.service';
import { fakeAsync, flushMicrotasks } from '@angular/core/testing';

describe('ConsoleInterceptorBypassService', () => {

    it('Should relay messages', fakeAsync(() => {
        const sut = new ConsoleInterceptorBypassService();

        const handleSpy = jasmine.createSpy();
        sut.getMessages$().subscribe( m => handleSpy(m.method));

        sut.log('stuff');
        sut.warn('stuff');
        sut.error('stuff');
        sut.debug('stuff');
        sut.info('stuff');

        flushMicrotasks();

        expect(handleSpy).toHaveBeenCalledWith('log');
        expect(handleSpy).toHaveBeenCalledWith('warn');
        expect(handleSpy).toHaveBeenCalledWith('error');
        expect(handleSpy).toHaveBeenCalledWith('debug');
        expect(handleSpy).toHaveBeenCalledWith('info');
    }));
});
