import React from "react";
import { Outlet } from "react-router-dom";
import TopNav from "./components/TopNav";
import Sidebar from "./components/Sidebar";
import Footer from "./components/Footer";

export default function App() {
  return (
    <div className="app-root">
      <Sidebar />
      <div className="main-area">
        <TopNav />
        <div className="content-area">
          <Outlet />
        </div>
        <Footer />
      </div>
    </div>
  );
}
