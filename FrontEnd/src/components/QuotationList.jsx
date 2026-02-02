import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function QuotationList(){
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const nav = useNavigate();
  const API = import.meta.env.VITE_API_URL;

  useEffect(()=>{
    fetch();
  },[]);

  const fetch = async ()=>{
    try{
      const res = await axios.get(`${API}/quotations/`);
        (res.data || []);
        setRows(res.data)
    }catch(err){
      console.error(err);
      alert("Failed to load quotations");
    }finally{
      setLoading(false);
    }
  };

 const handleConfirm = async (id) => {
  try{
    const res = await axios.post(`${API}/billing/confirmFromQuotation/${id}`);
    const billId = res.data;
    nav(`/bills/${billId}`); // <-- use returned bill id
  } catch(e){
    console.error(e);
    alert("Failed to confirm order");
  }
};


  const openSend = (id) => {
    nav(`/quotations/${id}/send`);
  };

  return (
    <div>
      <div style={{display:"flex", justifyContent:"space-between", marginBottom:12}}>
        <h2>All Quotations</h2>
        <div>
          <button className="btn btn-primary" onClick={()=>nav("/quotations/new")}>+ New</button>
        </div>
      </div>

      <div className="card">
        {loading ? <div className="center">Loading...</div> :
          rows.length === 0 ? <div className="center small">No quotations</div> :
          <table className="table">
            <thead>
              <tr>
                <th>ID</th><th>Customer</th><th>WhatsApp</th><th>Email</th><th>Requirement</th><th>Status</th><th>Action</th>
              </tr>
            </thead>
            <tbody>
              {rows.map(q => (
                <tr key={q.id}>
                  <td>{q.id}</td>
                  <td>{q.customer?.name || q.name || "N/A"}</td>
                  <td>{q.customer?.whatsAppNo || q.whatsAppNo || "N/A"}</td>
                  <td>{q.customer?.email || q.email || "N/A"}</td>
                  <td>{q.initialRequirement || "N/A"}</td>
                  <td>{q.status || "Pending"}</td>
                  <td>
                    <div className="actions-row">
                      <button className="btn btn-ghost" onClick={()=>openSend(q.id)}>Send</button>
                      <button className="btn btn-primary" onClick={()=>handleConfirm(q.id)}>Confirm</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        }
      </div>
    </div>
  );
}
