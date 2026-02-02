import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function QuotationForm(){
  const [form, setForm] = useState({ name:"", email:"", whatsAppNo:"", initialRequirement:"" });
  const [msg, setMsg] = useState("");
  const nav = useNavigate();
  const API = import.meta.env.VITE_API_URL;

  const change = (e) => setForm({...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    try{
      // Backend expects QuotationDTO flattened shape
      await axios.post(`${API}/quotations/saveQuotation`, form);
      setMsg("Saved");
      setTimeout(()=> nav("/quotations"), 800);
    }catch(err){
      console.error(err);
      setMsg("Failed to save");
    }
  };

  return (
    <div>
      <h2>Create Quotation</h2>
      <div className="card" style={{maxWidth:720}}>
        <form onSubmit={submit}>
          <div className="form-row">
            <div className="field">
              <label>Customer name</label>
              <input name="name" value={form.name} onChange={change} required />
            </div>
            <div className="field">
              <label>WhatsApp No</label>
              <input name="whatsAppNo" value={form.whatsAppNo} onChange={change} required />
            </div>
          </div>

          <div className="form-row">
            <div className="field">
              <label>Email</label>
              <input name="email" value={form.email} onChange={change} type="email" />
            </div>
            <div className="field">
              <label>Requirement</label>
              <input name="initialRequirement" value={form.initialRequirement} onChange={change} />
            </div>
          </div>

          <div style={{display:"flex", gap:8}}>
            <button className="btn btn-primary" type="submit">Save</button>
            <button className="btn btn-ghost" type="button" onClick={()=>nav("/quotations")}>Cancel</button>
          </div>
        </form>

        {msg && <div style={{marginTop:12}} className="small">{msg}</div>}
      </div>
    </div>
  );
}
