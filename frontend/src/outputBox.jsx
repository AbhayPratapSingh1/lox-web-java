export const OutputBox = ({ output }) => {
  return (
    <div className="w-1/2 h-screen overflow-scroll border-l  p-2 text-sm pb-20">
      {output.errors.length > 0
        ? <Errors errors={output.errors} />
        : <Logs logs={output.logs} />}
    </div>
  );
  if (output.errors.length > 0) {
  }
  return <Logs logs={output.logs} />;
};

const Errors = ({ errors }) => {
  return <p className="text-red-600">{errors.join("\n")}</p>;
};

const Logs = ({ logs }) => {
  const parsedLogs = parseLogs(logs);

  return (
    <>
      {parsedLogs.map((line, key) => {
        return (
          <p key={key} className="text-gray-600 ">
            <span className="mx-1">{">>"}</span>
            <span className="mx-1">{line}</span>
          </p>
        );
      })}
    </>
  );
};

const parseLogs = (logs) => {
  return logs.reduce((log, line) => {
    if (line === "\n") {
      log.push("");
    } else {
      log[log.length - 1] = logs.at(-1) + line;
    }
    return log;
  }, [""]);
};
