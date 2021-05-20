import { IPlatformPlugin } from '../platform-plugins/platform-plugin.interface';
import { TestBed } from '@angular/core/testing';
import { PLUGINS, PluginStartupTask } from './plugin-startup-task';
import { of, Observable } from 'rxjs';
import { Type } from '@angular/core';
import { IStartupTask } from './startup-task.interface';
import { getTestScheduler } from 'jasmine-marbles';

describe('PluginStartupTask', () => {
    class TestPlugin implements IPlatformPlugin {
        constructor( private isPresent: boolean, private pluginName: string ) { }

        name(): string {
            return this.pluginName;
        }

        pluginPresent(): boolean {
            return this.isPresent;
        }

        setPluginPresent( present: boolean ) {
            this.isPresent = present;
        }

        initialize(): Observable<string> {
            return of(`${this.pluginName} initialized`);
        }
    }

    class TestPlugin1 extends TestPlugin {
        constructor() { super( true, 'TestPlugin1' ); }
    }

    class TestPlugin3 extends TestPlugin {
        constructor() { super( true, 'TestPlugin3' ); }
    }

    class TestPlugin2 extends TestPlugin {
        constructor() { super( false, 'TestPlugin2' ); }
    }

    let pluginProviders = [];

    function addPlugin(plugin: Type<IPlatformPlugin>) {
        pluginProviders.push(plugin);
        pluginProviders.push({provide: PLUGINS, useExisting: plugin, multi: true});
    }

    function setup() {
        TestBed.configureTestingModule({
            providers: [
                PluginStartupTask,
                ...pluginProviders
            ]
        });
    }

    function runTask(): string[] {
        const pluginStartupTask: IStartupTask = TestBed.get(PluginStartupTask);
        const messages = [];
        pluginStartupTask.execute().subscribe( m => {
            messages.push(m);
            console.log(m);
        });

        getTestScheduler().flush();

        return messages;
    }

    beforeEach(() => {
        pluginProviders = [];
        TestBed.resetTestingModule();
    });


    it('should initialize plugins that are present', () => {
        addPlugin(TestPlugin1);
        addPlugin(TestPlugin2);
        addPlugin(TestPlugin3);

        setup();

        const testPlugin1Mock = TestBed.get(TestPlugin1);
        spyOn(testPlugin1Mock, 'initialize').and.callThrough();
        const testPlugin3Mock = TestBed.get(TestPlugin3);
        spyOn(testPlugin3Mock, 'initialize').and.callThrough();

        runTask();

        expect(testPlugin1Mock.initialize).toHaveBeenCalled();
        expect(testPlugin3Mock.initialize).toHaveBeenCalled();
    });

    it('should remove plugins that are not present', () => {
        addPlugin(TestPlugin1);
        addPlugin(TestPlugin2);
        addPlugin(TestPlugin3);

        setup();

        const testPlugin2Mock = TestBed.get(TestPlugin2);
        spyOn(testPlugin2Mock, 'initialize');

        runTask();

        expect(testPlugin2Mock.initialize).not.toHaveBeenCalled();
    });

    it('should initialize all the plugins', () => {
        addPlugin(TestPlugin1);

        setup();

        const messages = runTask();

        expect(messages).toContain('TestPlugin1 initialized');
    });
});
