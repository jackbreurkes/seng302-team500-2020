/* eslint-disable no-unused-vars */
/* eslint-env jest */

import log from "./logger.service";

let logOutput = [];
let warnOutput = [];
let errorOutput = [];
const mockedLog = (output) => logOutput.push(output);
const mockedWarn = (output) => warnOutput.push(output);
const mockedError = (output) => errorOutput.push(output);

beforeEach(() => {
    logOutput = [];
    warnOutput = [];
    errorOutput = [];
    console.log = mockedLog;
    console.warn = mockedWarn;
    console.error = mockedError;
})

test(
    "expect log to use console.log by default if NODE_ENV is not production", () => {
        const message = "message";
        log(message);
        expect(logOutput).toEqual(
            [message]
        )
    }
);

test(
    "expect log to use console.log for argument 'info' if NODE_ENV is not production", () => {
        const message = "message";
        log(message, "info");
        expect(logOutput).toEqual(
            [message]
        )
    }
);

test(
    "expect log to use console.warn for argument 'warn' if NODE_ENV is not production", () => {
        const message = "message";
        log(message, "warn");
        expect(warnOutput).toEqual(
            [message]
        )
    }
);

test(
    "expect log to use console.error for argument 'error' if NODE_ENV is not production", () => {
        const message = "message";
        log(message, "error");
        expect(errorOutput).toEqual(
            [message]
        )
    }
);

test.each(["info", "warn", "error"])(
    "expect log to not log anything to console if NODE_ENV is production", (logLevel) => {
        process.env.NODE_ENV = "production"
        const message = "message";
        log(message, logLevel);
        expect(logOutput).toEqual(
            []
        );
        expect(warnOutput).toEqual(
            []
        );
        expect(errorOutput).toEqual(
            []
        );
    }
);
