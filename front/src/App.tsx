import React from "react";
import "./App.css";
import { HashRouter, Navigate, Route, Routes } from "react-router-dom";
import { Visualization } from "./Visualization";
import "@fontsource/roboto/300.css";
import "@fontsource/roboto/400.css";
import "@fontsource/roboto/500.css";
import "@fontsource/roboto/700.css";

function App() {
  return (
    <HashRouter>
      <Routes>
        <Route
          path="/"
          element={<Navigate to="/problems/1" replace={true} />}
        ></Route>
        <Route
          path="problems/:problemId/solutions/:solutionId"
          element={<Visualization />}
        />
        <Route path="problems/:problemId" element={<Visualization />} />
      </Routes>
    </HashRouter>
  );
}

export default App;
