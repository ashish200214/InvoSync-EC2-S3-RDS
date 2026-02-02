import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

export default function BillingList(){
  const [rows, setRows] = useState([]);
  const nav = useNavigate();
  const API = import.meta.env.VITE_API_URL;
  useEffect(()=>{
    axios.get(`${API}/billing/`) // adjust if your endpoint is different
      .then(r=> setRows(r.data || []))
      .catch(e=> console.error(e));
  },[]);

  return (
    <div>
      <div style={{display:"flex", justifyContent:"space-between"}}>
        <h2>Bills</h2>
      </div>

      <div className="card">
        {rows.length === 0 ? <div className="small">No bills yet</div> :
          <table className="table">
            <thead><tr><th>ID</th><th>Customer</th><th>Total</th><th>Remaining</th><th>Action</th></tr></thead>
            <tbody>
              {rows.map(b=>(
                <tr key={b.id}>
                  <td>{b.id}</td>
                  <td>{b.customer?.name || b.name}</td>
                  <td>₹{b.total?.toFixed(2)}</td>
                  <td>₹{b.remainingAmount?.toFixed(2)}</td>
                  <td>
                    <div className="actions-row">
                      <button className="btn btn-ghost" onClick={()=>nav(`/bills/${b.id}`)}>View</button>
                      <a className="btn btn-primary" href={b.pdfUrl} target="_blank" rel="noreferrer">Download PDF</a>
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
