import { useState } from "react";
import CodeBox from "./codeBox";
import { OutputBox } from "./outputBox";

function App() {
  const [lines, setLines] = useState([""]);
  const [output, setOutput] = useState({ logs: [], errors: [] });
  const [loading, setLoading] = useState(false);

  const handleCodeRun = async () => {
    setLoading(true);

    const res = await fetch(`/api/run-program`, {
      method: "POST",
      headers: {
        "content-type": "application/json",
      },
      body: JSON.stringify({ program: lines.join("\n") }),
    });

    if (res.ok) {
      const data = await res.json();
      setOutput(data);
      setLoading(false);
      return;
    }
    alert("something went wrong");
    setLoading(false);
  };

  return (
    <>
      <div className="w-screen h-screen  overflow-hidden flex flex-col ">
        <div className="p-1">
          <button
            disabled={loading}
            onClick={handleCodeRun}
            className="px-2 py-1 border rounded-md text-sm bg-blue-500 disabled:bg-gray-400  text-white border-gray-200"
          >
            Run
          </button>
        </div>

        <div className="w-full grow border-5 border-gray-300 flex ">
          <CodeBox lines={lines} setLines={setLines} />
          <OutputBox output={output} />
        </div>
      </div>
    </>
  );
}

export default App;
