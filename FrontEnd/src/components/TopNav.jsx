import React from "react";
import { useNavigate } from "react-router-dom";

export default function TopNav(){
  const nav = useNavigate();
  return (
    <div className="topnav">
      <div className="left">
        <h1>InfoSync Dashboard</h1>
        <div className="small">Manage quotations & billing</div>
      </div>
      <div className="actions">
        <button className="btn btn-ghost" onClick={()=>nav("/quotations/new")}>+ New Quotation</button>
        <button className="btn" onClick={()=>nav("/quotations")}>Quotations</button>
        <button className="btn" onClick={()=>nav("/bills")}>Bills</button>
      </div>
    </div>
  );
}
