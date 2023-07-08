import React from "react";
import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Visualization } from "./Visualization";
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="problems/:problemId/solutions/:solutionId" element={<Visualization/>}>
        </Route>
        <Route path="problems/:problemId" element={<Visualization/>}>
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
