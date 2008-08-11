var newLevel = args[0];
print("update all logger to " + newLevel);

// retrieve the Logging MBean

var logging = mbsc.getMBean('java.util.logging:type=Logging');
var loggers = logging.loggerNames;

print("==== before ====");
// display the current loggers'level and update it
for (var i = 0 ; i < loggers.length; i++) {
    print(loggers[i] + ' ' + logging.getLoggerLevel(loggers[i]));
    logging.setLoggerLevel(loggers[i], newLevel);
}

print("==== after ====");
// display again the loggers'level which have been updated
for (var i = 0 ; i < loggers.length; i++) {
    print(loggers[i] + ' ' + logging.getLoggerLevel(loggers[i]));
}