import Editor from "@monaco-editor/react";

export default function CodeBox({ lines, setLines }) {
  const value = lines.join("\n");

  const handleChange = (newValue) => {
    setLines((newValue || "").split("\n"));
  };

  return (
    <div className="w-1/2 p-0">
      <Editor
        height="100%"
        value={value}
        onChange={handleChange}
        options={{
          padding: { top: 8, bottom: 8 },
          lineNumbers: "on",
          glyphMargin: false,
          folding: false,
          minimap: { enabled: false },
          scrollBeyondLastLine: false,
          automaticLayout: true,

          lineDecorationsWidth: 10,
          lineNumbersMinChars: 2,
        }}
      />
    </div>
  );
}
