import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import { jsPDF } from "jspdf";

export default function SendQuotation(){
  const { id } = useParams();
  const nav = useNavigate();

  const [quotation, setQuotation] = useState(null);
  const [items, setItems] = useState([{ name:"", qty:1, price:0 }]);
  const [drawing, setDrawing] = useState(null);
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");
  const API = import.meta.env.VITE_API_URL;

  useEffect(()=>{
    axios.get(`${API}/quotations/${id}`)
      .then(r => {
        setQuotation(r.data);
        if(r.data.items && r.data.items.length){
          // map backend items if present
          const mapped = r.data.items.map(it => ({
            name: it.particular || it.name || "",
            qty: it.quantity || it.qty || 1,
            price: it.price || it.rate || (it.total ? (it.total) : 0)
          }));
          setItems(mapped);
        }
      })
      .catch(e => { console.error(e); setMsg("Failed to load quotation"); });
  },[id]);

  const handleItemChange = (idx, field, value) => {
    const copy = [...items]; copy[idx][field] = field === "name" ? value : Number(value) || 0; setItems(copy);
  };
  const addItem = () => setItems([...items, {name:"", qty:1, price:0}]);
  const removeItem = (idx) => setItems(items.filter((_,i)=>i!==idx));

  const grandTotal = items.reduce((s,it)=> s + (it.qty||0)*(it.price||0), 0);

 function generatePdfBlob() {
  const doc = new jsPDF({ unit: "pt", format: "a4" });

  // header
  doc.setFillColor(14,122,255);
  doc.rect(0,0,595,72,"F"); // A4 width ~595pt
  doc.setFont("helvetica", "bold");
  doc.setFontSize(20);
  doc.setTextColor(255,255,255);
  doc.text("QUOTATION", 40, 44);

  doc.setFont("helvetica","normal");
  doc.setFontSize(11);
  doc.setTextColor(30,41,59);

  const yStart = 100;
  doc.text(`Customer: ${quotation?.customer?.name || quotation?.name || ""}`, 40, yStart);
  doc.text(`Email: ${quotation?.customer?.email || quotation?.email || ""}`, 40, yStart + 16);
  doc.text(`WhatsApp: ${quotation?.customer?.whatsAppNo || quotation?.whatsAppNo || ""}`, 40, yStart + 32);

  // table header
  let y = yStart + 60;
  doc.setFont("helvetica","bold");
  doc.setTextColor(14,122,255);
  doc.text("Sr", 40, y);
  doc.text("Particular", 80, y);
  doc.text("Qty", 380, y);
  doc.text("Price", 430, y);
  doc.text("Total", 510, y);

  doc.setLineWidth(0.5);
  doc.setDrawColor(220, 227, 235);
  doc.line(40, y+4, 555, y+4);
  y += 18;
  doc.setFont("helvetica","normal");
  doc.setTextColor(30,41,59);

  items.forEach((it, idx) => {
    if(y > 740){ doc.addPage(); y = 60; }
    doc.text(String(idx+1), 40, y);
    doc.text(String(it.name || ""), 80, y, { maxWidth: 280 });
    doc.text(String(it.qty), 380, y);
    doc.text((Number(it.price) || 0).toFixed(2), 430, y);
    doc.text(((it.qty||0)*(it.price||0)).toFixed(2), 510, y);
    y += 18;
  });

  y += 18;
  doc.setFont("helvetica","bold");
  doc.setTextColor(14,122,255);

  // *** THIS IS THE FIXED LINE ***
  // Replaced '₹' with 'Rs.' to fix the font issue
  doc.text(`Grand Total: Rs. ${grandTotal.toFixed(2)}`, 40, y);

  return doc.output("blob");
}

  const handleSubmit = async (e) => {
    e.preventDefault();
    if(!quotation) { setMsg("Quotation not loaded"); return; }

    setLoading(true);
    setMsg("");

    try{
      const pdfBlob = generatePdfBlob();
      const pdfFile = new File([pdfBlob], `quotation_${id}.pdf`, { type: "application/pdf" });

      const fd = new FormData();
      fd.append("quotationPdf", pdfFile);
      if(drawing) fd.append("drawing", drawing);
      images.forEach(img => fd.append("images", img));

      // send to backend endpoint (multipart). backend expects path: /api/quotations/{id}/send
      const res = await axios.post(`${API}/quotations/${id}/send`, fd);

      setMsg(res.data || "Quotation sent");
    }catch(err){
      console.error(err);
      setMsg("Failed to send quotation");
    }finally{
      setLoading(false);
    }
  };

  if(!quotation) return <div className="card center">Loading quotation...</div>;

  return (
    <div className="card send-quotation">
      <h2>Send Quotation{id}</h2>

      <div style={{marginBottom:12}}>
        <strong>Customer:</strong> {quotation.customer?.name || quotation.name}
        <div className="small">{quotation.customer?.email || quotation.email}</div>
      </div>

      <form onSubmit={handleSubmit}>
        <div>
          <h4>Items</h4>
          {items.map((it, idx) => (
            <div className="item-row" key={idx}>
              <input placeholder="Particular" value={it.name} onChange={e=>handleItemChange(idx,"name", e.target.value)} />
              <input type="number" style={{width:80}} value={it.qty} onChange={e=>handleItemChange(idx,"qty", Number(e.target.value))} />
              <input type="number" style={{width:120}} value={it.price} onChange={e=>handleItemChange(idx,"price", Number(e.target.value))} />
              {/* This '₹' is fine because it's in HTML, not the PDF */}
              <div style={{width:120, textAlign:"right", paddingRight:8}}>₹{((it.qty||0)*(it.price||0)).toFixed(2)}</div>
              <button type="button" className="btn" onClick={()=>removeItem(idx)}>✖</button>
            </div>
          ))}

          <div style={{marginTop:8}}>
            <button type="button" className="btn btn-ghost" onClick={addItem}>+ Add item</button>
          </div>
        </div>

        <div style={{marginTop:14}}>
          <label>Upload drawing (optional)</label>
          <input type="file" accept=".pdf,.dwg,.dxf,.cdr,.png,.jpg" onChange={e=>setDrawing(e.target.files[0])} />
        </div>

        <div style={{marginTop:12}}>
          <label>Upload images (optional)</label>
          <input type="file" multiple accept="image/*" onChange={e=>setImages([...e.target.files])} />
        </div>

        <div style={{marginTop:16, display:"flex", gap:8}}>
          <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? "Sending..." : "Send Quotation"}</button>
          <button className="btn btn-ghost" type="button" onClick={()=>nav("/quotations")}>Back</button>
        </div>
      </form>

      {msg && <div style={{marginTop:12}} className="small">{msg}</div>}
    </div>
  );
}
