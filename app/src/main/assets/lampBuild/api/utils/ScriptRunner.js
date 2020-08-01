"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
    return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (_) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
var __values = (this && this.__values) || function(o) {
    var s = typeof Symbol === "function" && Symbol.iterator, m = s && o[s], i = 0;
    if (m) return m.call(o);
    if (o && typeof o.length === "number") return {
        next: function () {
            if (o && i >= o.length) o = void 0;
            return { value: o && o[i++], done: !o };
        }
    };
    throw new TypeError(s ? "Object is not iterable." : "Symbol.iterator is not defined.");
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
var app_1 = require("../app");
var tar_stream_1 = __importDefault(require("tar-stream"));
var vm2_1 = __importDefault(require("vm2"));
// TODO: USE DOCKER COMMIT!
//console.dir(await ((new ScriptRunner.JS()).execute('console.log(\'testing\'); return "hi";', '', '')))
//console.dir(await ((new ScriptRunner.Bash()).execute('echo "Hello World"', '', '')))
/**
 *
 */
var ScriptRunner = /** @class */ (function () {
    function ScriptRunner() {
    }
    /**
     * Create a simple sequential task queue to manage script invocations.
     * FIXME: It's concurrent. :(
     */
    ScriptRunner.queue = {
        _running: false,
        _store: [],
        _exec: function () {
            if (!ScriptRunner.queue._running && ScriptRunner.queue._store.length > 0) {
                ScriptRunner.queue._running = true;
                console.group("ScriptRunner::TaskQueue");
                console.log("current pressure = " + ScriptRunner.queue._store.length);
                var task = ScriptRunner.queue._store.shift();
                task();
                console.groupEnd();
                ScriptRunner.queue._running = false;
                ScriptRunner.queue._exec();
            }
        },
        enqueue: function (task) {
            ScriptRunner.queue._store.push(task);
            if (!ScriptRunner.queue._running)
                ScriptRunner.queue._exec();
        }
    };
    /**
     *
     */
    ScriptRunner.Bash = /** @class */ (function (_super) {
        __extends(class_1, _super);
        function class_1() {
            return _super !== null && _super.apply(this, arguments) || this;
        }
        class_1.prototype.execute = function (script, requirements, input) {
            return __awaiter(this, void 0, void 0, function () {
                var _this = this;
                return __generator(this, function (_a) {
                    return [2 /*return*/, new Promise(function (resolve, reject) {
                            return ScriptRunner.queue.enqueue(function () { return __awaiter(_this, void 0, void 0, function () {
                                var exists, image_1, container, logs, _a, _b, output, _c, e_1;
                                return __generator(this, function (_d) {
                                    switch (_d.label) {
                                        case 0:
                                            console.group("ScriptRunner.Bash");
                                            return [4 /*yield*/, app_1.Docker.listImages({ filters: { reference: ["alpine:latest"] } })];
                                        case 1:
                                            exists = _d.sent();
                                            if (!(exists.length === 0)) return [3 /*break*/, 4];
                                            console.log("Creating docker image...");
                                            return [4 /*yield*/, app_1.Docker.pull("alpine:latest", {})];
                                        case 2:
                                            image_1 = _d.sent();
                                            return [4 /*yield*/, new Promise(function (resolve, reject) {
                                                    image_1.pipe(process.stdout);
                                                    image_1.on("end", resolve);
                                                    image_1.on("error", reject);
                                                })];
                                        case 3:
                                            _d.sent();
                                            _d.label = 4;
                                        case 4:
                                            // Create and start a new container...
                                            console.log("Creating docker container...");
                                            return [4 /*yield*/, app_1.Docker.createContainer({
                                                    Image: "alpine:latest",
                                                    Tty: true,
                                                    Cmd: ["/bin/sh"]
                                                })];
                                        case 5:
                                            container = _d.sent();
                                            return [4 /*yield*/, container.start()
                                                // First configure the environment and packages with network available.
                                            ];
                                        case 6:
                                            _d.sent();
                                            logs = [];
                                            _d.label = 7;
                                        case 7:
                                            _d.trys.push([7, 12, 13, 16]);
                                            // Place input files, call the main script, and grab the output files.
                                            console.log("Configuring script...");
                                            return [4 /*yield*/, container.putArchive(makeTar({
                                                    "/src/script": script
                                                }), { path: "/" })];
                                        case 8:
                                            _d.sent();
                                            _b = (_a = logs).push;
                                            return [4 /*yield*/, containerExec(container, "touch /src/stdout && chmod +x /src/script && /src/script")];
                                        case 9:
                                            _b.apply(_a, [_d.sent()]);
                                            console.log("Retrieving result...");
                                            _c = getFileInTar;
                                            return [4 /*yield*/, container.getArchive({ path: "/src/stdout" })];
                                        case 10: return [4 /*yield*/, _c.apply(void 0, [_d.sent(), "stdout"])];
                                        case 11:
                                            output = (_d.sent()).toString("utf8");
                                            resolve({ output: output, logs: Buffer.concat(logs).toString("utf8") });
                                            return [3 /*break*/, 16];
                                        case 12:
                                            e_1 = _d.sent();
                                            console.error(e_1);
                                            reject(e_1);
                                            return [3 /*break*/, 16];
                                        case 13:
                                            console.log("Cleaning up...");
                                            console.groupEnd();
                                            //
                                            return [4 /*yield*/, container.stop()];
                                        case 14:
                                            //
                                            _d.sent();
                                            return [4 /*yield*/, container.remove({ force: true })];
                                        case 15:
                                            _d.sent();
                                            return [7 /*endfinally*/];
                                        case 16: return [2 /*return*/];
                                    }
                                });
                            }); });
                        })];
                });
            });
        };
        return class_1;
    }(ScriptRunner));
    /**
     * RScript @ Ubuntu 18.04 LTS + R 3.5.1
     */
    ScriptRunner.R = /** @class */ (function (_super) {
        __extends(class_2, _super);
        function class_2() {
            return _super !== null && _super.apply(this, arguments) || this;
        }
        class_2.prototype.execute = function (script, requirements, input) {
            return __awaiter(this, void 0, void 0, function () {
                var Dockerfile, driverScript;
                var _this = this;
                return __generator(this, function (_a) {
                    Dockerfile = "\n\t\t\tFROM ubuntu:18.04\n\t\t\tENV DEBIAN_FRONTEND noninteractive\n\n\t\t\tRUN useradd docker \t\t\t\t&& mkdir /home/docker \t\t\t\t&& chown docker:docker /home/docker \t\t\t\t&& addgroup docker staff\n\n\t\t\tRUN apt-get update \t\t\t\t&& apt-get install -y --no-install-recommends \t\t\t\t\tsocat \t\t\t\t\tcurl \t\t\t\t\tlibcurl4-openssl-dev \t\t\t\t\tsoftware-properties-common \t\t\t\t\ted \t\t\t\t\tless \t\t\t\t\tlocales \t\t\t\t\tvim-tiny \t\t\t\t\twget \t\t\t\t\tca-certificates \t\t\t\t&& add-apt-repository --enable-source --yes \"ppa:marutter/rrutter3.5\" \t\t\t\t&& add-apt-repository --enable-source --yes \"ppa:marutter/c2d4u3.5\" \n\n\t\t\tRUN echo \"en_US.UTF-8 UTF-8\" >> /etc/locale.gen \t\t\t\t&& locale-gen en_US.utf8 \t\t\t\t&& /usr/sbin/update-locale LANG=en_US.UTF-8\n\t\t\tENV LC_ALL en_US.UTF-8\n\t\t\tENV LANG en_US.UTF-8\n\n\t\t\tENV R_LIBS_SITE /usr/lib/R/library\n\t\t\tRUN mkdir -p /usr/lib/R/library && mkdir -p /usr/local/lib/R/ \t\t\t\t&& ln -s /usr/lib/R/library /usr/lib/R/site-library \t\t\t\t&& ln -s /usr/lib/R/library /usr/local/lib/R/site-library \n\n\t\t\tRUN apt-get update \t\t\t        && apt-get install -y --no-install-recommends \t\t\t \t\t r-base \t\t\t \t\t r-base-dev \t\t\t \t\t r-recommended \t\t\t         r-cran-rcpp \t\t\t         r-cran-jsonlite \t\t\t \t\t littler \t\t\t         libxml2-dev \t\t\t         libcairo2-dev \t\t\t         libssl-dev \t\t\t         libcurl4-openssl-dev \t\t\t    && /usr/lib/R/library/littler/examples/install.r docopt \t\t\t \t&& rm -rf /tmp/downloaded_packages/ /tmp/*.rds \t\t\t \t&& rm -rf /var/lib/apt/lists/*\n\n\t\t\tRUN apt-get update \t\t\t\t\t&& apt-get install -y --no-install-recommends \t\t\t\t\t python python-pip python3-pip python-dev \t\t\t \t&& rm -rf /tmp/downloaded_packages/ /tmp/*.rds \t\t\t \t&& rm -rf /var/lib/apt/lists/*\n\n\t\t\tCMD [\"/bin/bash\"]";
                    driverScript = "\n\t\t\tlibrary(jsonlite)\n\t\t\toptions(error=function() traceback(2))\n\n\t\t\t# Execute the script with wrapped I/O + logging.\n\t\t\tLAMP_input <<- fromJSON(file('/src/input'))\n\t\t\tcommandArgs <- function(...) LAMP_input\n\t\t\tsource('/src/script.r', print.eval=TRUE)\n\t\t\tLAMP_output <<- get('value', .Last.value)\n\n\t\t\t# If the result was a plot, save and read the file as a blob.\n\t\t\tif ('ggplot' %in% class(LAMP_output)) {\n\t\t\t    get <- function(x, z) if(x %in% names(LAMP_input$`_plot`)) LAMP_input$`_plot`[[x]] else z\n\n\t\t\t    # Perform pixel <-> DPI translation based on img device and scale.\n\t\t\t    type <- get('type', 'png')\n\t\t\t    dpi <- 100 * get('scale', 1)\n\t\t\t    width <- get('width', 800) / dpi\n\t\t\t    height <- get('height', 600) / dpi\n\n\t\t\t    ggsave('tmp.out', device=type, dpi=dpi, width=width, height=height)\n\t\t\t    LAMP_output <<- readBin('tmp.out', 'raw', file.info('tmp.out')$size)\n\t\t\t}\n\n\t\t\t# Serialize output to JSON or reinterpret if possible.\n\t\t\twrite(tryCatch({\n\t\t\t    toJSON(LAMP_output, auto_unbox=TRUE)\n\t\t\t}, error = function(err) {\n\t\t\t    return(serializeJSON(LAMP_output))\n\t\t\t}), '/src/stdout')";
                    return [2 /*return*/, new Promise(function (resolve, reject) {
                            return ScriptRunner.queue.enqueue(function () { return __awaiter(_this, void 0, void 0, function () {
                                var exists, image_2, res, container, logs, _a, _b, _c, _d, output, _e, e_2;
                                return __generator(this, function (_f) {
                                    switch (_f.label) {
                                        case 0:
                                            console.group("ScriptRunner.R");
                                            return [4 /*yield*/, app_1.Docker.listImages({ filters: { reference: ["lamp:latest"] } })];
                                        case 1:
                                            exists = _f.sent();
                                            if (!(exists.length === 0)) return [3 /*break*/, 5];
                                            console.log("Creating docker image...");
                                            return [4 /*yield*/, app_1.Docker.buildImage(makeTar({ Dockerfile: Dockerfile }), { t: "lamp:latest" })];
                                        case 2:
                                            image_2 = _f.sent();
                                            return [4 /*yield*/, new Promise(function (resolve, reject) {
                                                    image_2.pipe(process.stdout);
                                                    image_2.on("end", resolve);
                                                    image_2.on("error", reject);
                                                })
                                                // Migrate packages to the shared volume/bind-mount.
                                                // This can't be done from within a Docker build context.
                                            ];
                                        case 3:
                                            _f.sent();
                                            return [4 /*yield*/, systemDocker("mv /usr/lib/R/library/* /usr/lib/R/library2", {
                                                    Image: "lamp:latest",
                                                    Tty: true,
                                                    Cmd: ["/bin/bash"],
                                                    HostConfig: {
                                                        Binds: ["/src/r-libs:/usr/lib/R/library2"]
                                                    }
                                                })];
                                        case 4:
                                            res = _f.sent();
                                            console.log(res.toString("utf8"));
                                            _f.label = 5;
                                        case 5:
                                            // Create and start a new container...
                                            console.log("Creating docker container...");
                                            return [4 /*yield*/, app_1.Docker.createContainer({
                                                    Image: "lamp:latest",
                                                    Tty: true,
                                                    Cmd: ["/bin/bash"],
                                                    HostConfig: {
                                                        Binds: ["/src/r-libs:/usr/lib/R/library"]
                                                    }
                                                })];
                                        case 6:
                                            container = _f.sent();
                                            return [4 /*yield*/, container.start()
                                                // First configure the R environment and packages with network available.
                                            ];
                                        case 7:
                                            _f.sent();
                                            logs = [];
                                            _f.label = 8;
                                        case 8:
                                            _f.trys.push([8, 15, 16, 19]);
                                            if (!(requirements.split(",").length > 0)) return [3 /*break*/, 10];
                                            console.log("Installing packages...");
                                            _b = (_a = logs).push;
                                            return [4 /*yield*/, containerExec(container, "apt-get update -qq && apt-get install -y " + requirements
                                                    .split(",")
                                                    .map(function (x) { return "r-cran-" + x.trim(); })
                                                    .join(" ") + "; ls /usr/lib/R/site-library/; /usr/lib/R/site-library/littler/examples/install2.r -s " + requirements
                                                    .split(",")
                                                    .join(" "))];
                                        case 9:
                                            _b.apply(_a, [_f.sent()]);
                                            _f.label = 10;
                                        case 10:
                                            // Place input files, call the main script, and grab the output files.
                                            console.log("Configuring script...");
                                            return [4 /*yield*/, container.putArchive(makeTar({
                                                    "/src/main.r": driverScript,
                                                    "/src/script.r": script,
                                                    "/src/input": input
                                                }), { path: "/" })];
                                        case 11:
                                            _f.sent();
                                            _d = (_c = logs).push;
                                            return [4 /*yield*/, containerExec(container, "touch /src/stdout && Rscript /src/main.r")];
                                        case 12:
                                            _d.apply(_c, [_f.sent()]);
                                            console.log("Retrieving result...");
                                            _e = getFileInTar;
                                            return [4 /*yield*/, container.getArchive({ path: "/src/stdout" })];
                                        case 13: return [4 /*yield*/, _e.apply(void 0, [_f.sent(), "stdout"])];
                                        case 14:
                                            output = (_f.sent()).toString("utf8");
                                            resolve({ output: output, logs: Buffer.concat(logs).toString("utf8") });
                                            return [3 /*break*/, 19];
                                        case 15:
                                            e_2 = _f.sent();
                                            console.error(e_2);
                                            reject(e_2);
                                            return [3 /*break*/, 19];
                                        case 16:
                                            console.log("Cleaning up...");
                                            console.groupEnd();
                                            //
                                            return [4 /*yield*/, container.stop()];
                                        case 17:
                                            //
                                            _f.sent();
                                            return [4 /*yield*/, container.remove({ force: true })];
                                        case 18:
                                            _f.sent();
                                            return [7 /*endfinally*/];
                                        case 19: return [2 /*return*/];
                                    }
                                });
                            }); });
                        })];
                });
            });
        };
        return class_2;
    }(ScriptRunner));
    /**
     *
     */
    ScriptRunner.Py = /** @class */ (function (_super) {
        __extends(class_3, _super);
        function class_3() {
            return _super !== null && _super.apply(this, arguments) || this;
        }
        class_3.prototype.execute = function (script, requirements, input) {
            return __awaiter(this, void 0, void 0, function () {
                return __generator(this, function (_a) {
                    throw Error("Unimplemented ScriptRunner!");
                });
            });
        };
        return class_3;
    }(ScriptRunner));
    /**
     *
     */
    ScriptRunner.JS = /** @class */ (function (_super) {
        __extends(class_4, _super);
        function class_4() {
            return _super !== null && _super.apply(this, arguments) || this;
        }
        class_4.prototype.execute = function (script, requirements, input) {
            return __awaiter(this, void 0, void 0, function () {
                var _this = this;
                return __generator(this, function (_a) {
                    return [2 /*return*/, new Promise(function (resolve, reject) {
                            return ScriptRunner.queue.enqueue(function () { return __awaiter(_this, void 0, void 0, function () {
                                var options;
                                return __generator(this, function (_a) {
                                    options = {
                                        console: "redirect",
                                        wrapper: "none",
                                        require: {
                                            external: false,
                                            builtin: ["http", "url"],
                                            import: ["http", "url"]
                                        }
                                    };
                                    resolve(new vm2_1.default.NodeVM(options).run(script, ""));
                                    return [2 /*return*/];
                                });
                            }); });
                        })];
                });
            });
        };
        return class_4;
    }(ScriptRunner));
    return ScriptRunner;
}());
exports.default = ScriptRunner;
/**
 *
 */
var containerExec = function (container, shellCommand) {
    return new Promise(function (resolve, error) {
        container.exec({ Cmd: ["/bin/sh", "-c", shellCommand], AttachStdout: true, AttachStderr: true }, function (cErr, exec) {
            if (cErr)
                return error(cErr);
            exec.start({ hijack: true }, function (sErr, stream) {
                if (sErr)
                    return error(sErr);
                var output = [];
                stream.on("data", function (chunk) {
                    chunk = chunk.slice(8);
                    console.log(chunk.toString("utf8"));
                    output.push(chunk);
                });
                stream.on("end", function () { return resolve(Buffer.concat(output)); });
            });
        });
    });
};
/**
 *
 */
var makeTar = function (data, dirPrefix) {
    var e_3, _a;
    if (dirPrefix === void 0) { dirPrefix = ""; }
    var pack = tar_stream_1.default.pack();
    try {
        for (var _b = __values(Object.entries(data)), _c = _b.next(); !_c.done; _c = _b.next()) {
            var x = _c.value;
            pack.entry({ name: dirPrefix + x[0] }, typeof x[1] === "string" ? x[1] : JSON.stringify(x[1]));
        }
    }
    catch (e_3_1) { e_3 = { error: e_3_1 }; }
    finally {
        try {
            if (_c && !_c.done && (_a = _b.return)) _a.call(_b);
        }
        finally { if (e_3) throw e_3.error; }
    }
    pack.finalize();
    return pack;
};
/**
 *
 */
var getFileInTar = function (tarStream, filename) { return __awaiter(void 0, void 0, void 0, function () {
    return __generator(this, function (_a) {
        return [2 /*return*/, new Promise(function (resolve, reject) {
                var extract = tar_stream_1.default.extract();
                var data = [];
                extract.on("entry", function (header, stream, next) {
                    if (header.name !== filename)
                        next();
                    stream.on("data", function (chunk) { return data.push(chunk); });
                    stream.on("end", next);
                    stream.resume();
                });
                extract.on("finish", function () { return resolve(Buffer.concat(data)); });
                tarStream.pipe(extract);
            })];
    });
}); };
/**
 *
 */
var systemDocker = function (command, options) { return __awaiter(void 0, void 0, void 0, function () {
    var container, output;
    return __generator(this, function (_a) {
        switch (_a.label) {
            case 0: return [4 /*yield*/, app_1.Docker.createContainer(options)];
            case 1:
                container = _a.sent();
                return [4 /*yield*/, container.start()];
            case 2:
                _a.sent();
                return [4 /*yield*/, containerExec(container, command)];
            case 3:
                output = _a.sent();
                return [4 /*yield*/, container.stop()];
            case 4:
                _a.sent();
                return [4 /*yield*/, container.remove({ force: true })];
            case 5:
                _a.sent();
                return [2 /*return*/, output];
        }
    });
}); };
/**
 * `await delay(x)`
 */
var delay = function (t) { return new Promise(function (resolve) { return setTimeout(resolve, t); }); };
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJmaWxlIjoiU2NyaXB0UnVubmVyLmpzIiwic291cmNlUm9vdCI6IiIsInNvdXJjZXMiOlsiLi4vLi4vc3JjL3V0aWxzL1NjcmlwdFJ1bm5lci50cyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiOzs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7Ozs7OztBQUFBLDhCQUErQjtBQUcvQiwwREFBNEI7QUFJNUIsNENBQXFCO0FBRXJCLDJCQUEyQjtBQUUzQix3R0FBd0c7QUFDeEcsc0ZBQXNGO0FBRXRGOztHQUVHO0FBQ0g7SUFBQTtJQTZUQSxDQUFDO0lBNVRDOzs7T0FHRztJQUNJLGtCQUFLLEdBQUc7UUFDYixRQUFRLEVBQUUsS0FBSztRQUNmLE1BQU0sRUFBOEIsRUFBRTtRQUN0QyxLQUFLLEVBQUU7WUFDTCxJQUFJLENBQUMsWUFBWSxDQUFDLEtBQUssQ0FBQyxRQUFRLElBQUksWUFBWSxDQUFDLEtBQUssQ0FBQyxNQUFNLENBQUMsTUFBTSxHQUFHLENBQUMsRUFBRTtnQkFDeEUsWUFBWSxDQUFDLEtBQUssQ0FBQyxRQUFRLEdBQUcsSUFBSSxDQUFBO2dCQUNsQyxPQUFPLENBQUMsS0FBSyxDQUFDLHlCQUF5QixDQUFDLENBQUE7Z0JBQ3hDLE9BQU8sQ0FBQyxHQUFHLENBQUMsd0JBQXNCLFlBQVksQ0FBQyxLQUFLLENBQUMsTUFBTSxDQUFDLE1BQVEsQ0FBQyxDQUFBO2dCQUNyRSxJQUFJLElBQUksR0FBRyxZQUFZLENBQUMsS0FBSyxDQUFDLE1BQU0sQ0FBQyxLQUFLLEVBQUcsQ0FBQTtnQkFDN0MsSUFBSSxFQUFFLENBQUE7Z0JBQ04sT0FBTyxDQUFDLFFBQVEsRUFBRSxDQUFBO2dCQUNsQixZQUFZLENBQUMsS0FBSyxDQUFDLFFBQVEsR0FBRyxLQUFLLENBQUE7Z0JBQ25DLFlBQVksQ0FBQyxLQUFLLENBQUMsS0FBSyxFQUFFLENBQUE7YUFDM0I7UUFDSCxDQUFDO1FBQ0QsT0FBTyxFQUFFLFVBQUMsSUFBeUI7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