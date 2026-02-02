import React, { useEffect, useState } from "react";
import axios from "axios";
import { useParams, useNavigate } from "react-router-dom";
import jsPDF from "jspdf";
import "./BillingPage.css"; // ensure path correct

export default function BillingPage(){
  const { id } = useParams();
  const nav = useNavigate();

  const [data, setData] = useState(null);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState("");
  const API = import.meta.env.VITE_API_URL;

  useEffect(() => {
    console.log("BillingPage mount, id =", id);
    if(!id){
      setMsg("Missing bill id in URL");
      setLoading(false);
      return;
    }

    axios.get(`${API}/billing/${id}`)
      .then(r => {
        console.log("GET /api/billing/:id response:", r.data);
        setData(r.data);
        if(r.data && Array.isArray(r.data.items) && r.data.items.length){
          const mapped = r.data.items.map(it => ({
            name: it.particular || it.name || "",
            qty: it.quantity || it.qty || 1,
            price: it.price || it.rate || 0
          }));
          setItems(mapped);
        } else {
          setItems([{ name:"", qty:1, price:0 }]);
        }
      })
      .catch(e => {
        console.error("Failed loading bill:", e);
        setMsg("Failed to load bill (see console)");
      })
      .finally(() => setLoading(false));
  }, [id]);

  const handleItemChange = (idx, field, value) => {
    const copy = [...items];
    if(field === "name") copy[idx].name = value;
    else if(field === "qty") copy[idx].qty = Number(value) || 0;
    else if(field === "price") copy[idx].price = Number(value) || 0;
    setItems(copy);
  };

  const addItem = () => setItems([...items, { name:"", qty:1, price:0 }]);
  const removeItem = (idx) => setItems(items.filter((_,i) => i !== idx));

  const total = items.reduce((s,it) => s + (it.qty||0)*(it.price||0), 0);

function generatePdfBlob() {
  // use A4 points
  const doc = new jsPDF({ unit: "pt", format: "a4" });

  // header bar
  doc.setFillColor(14, 122, 255);
  doc.rect(0, 0, 595, 72, "F"); // full-width colored header
  doc.setFont("helvetica", "bold");
  doc.setFontSize(20);
  doc.setTextColor(255, 255, 255);
  doc.text("FINAL BILL", 40, 44);

  // basic text color for body
  doc.setFont("helvetica", "normal");
  doc.setFontSize(11);
  doc.setTextColor(30, 41, 59);

  const yStart = 100;
  doc.text(`Bill No: ${data?.id || id}`, 40, yStart);
  doc.text(`Date: ${new Date().toLocaleString()}`, 300, yStart);
  doc.text(`Customer: ${data?.customerName || data?.customer?.name || data?.name || ""}`, 40, yStart + 18);
  if (data?.customerEmail || data?.customer?.email) {
    doc.text(`Email: ${data.customerEmail || data.customer?.email}`, 40, yStart + 36);
  }
  if (data?.customer?.whatsAppNo || data.whatsAppNo) {
    doc.text(`WhatsApp: ${data.customer?.whatsAppNo || data.whatsAppNo}`, 300, yStart + 36);
  }

  // table header
  let y = yStart + 72;
  doc.setFont("helvetica", "bold");
  doc.setTextColor(14, 122, 255);
  doc.text("Sr", 40, y);
  doc.text("Particular", 80, y);
  doc.text("Qty", 380, y);
  doc.text("Price", 430, y);
  doc.text("Total", 510, y);

  // light separator
  doc.setLineWidth(0.5);
  doc.setDrawColor(220, 227, 235);
  doc.line(40, y + 4, 555, y + 4);

  y += 18;
  doc.setFont("helvetica", "normal");
  doc.setTextColor(30, 41, 59);

  // iterate items (either from state items or data.items)
  const srcItems = items && items.length ? items : (data?.items || []);
  srcItems.forEach((it, idx) => {
    if (y > 740) { doc.addPage(); y = 60; }
    const name = it.name ?? it.particular ?? "";
    const qty = Number(it.qty ?? it.quantity ?? 0);
    const price = Number(it.price ?? it.price ?? it.price ?? 0);
    const lineTotal = (qty || 0) * (price || 0);

    doc.text(String(idx + 1), 40, y);
    doc.text(String(name), 80, y, { maxWidth: 280 });
    doc.text(String(qty), 380, y);
    doc.text((Number(price) || 0).toFixed(2), 430, y);
    doc.text(lineTotal.toFixed(2), 510, y);

    y += 18;
  });

  // total
  y += 18;
  doc.setFont("helvetica", "bold");
  doc.setTextColor(14, 122, 255);
  doc.text(`Grand Total: Rs. ${total.toFixed(2)}`, 40, y + 12);

  // optional drawing image (if you want to embed and you have drawing url)
  // NOTE: jsPDF can add images only if loaded as dataURL or image element; if drawing URL is cross-origin,
  // you may need to fetch blob, convert to dataURL, then doc.addImage(...)
  // Example (async) omitted here — this function returns blob synchronously.

  return doc.output("blob");
}


  const sendBill = async () => {
    setMsg(""); setLoading(true);
    try{
      const blob = generatePdfBlob();
      const file = new File([blob], `bill_${id}.pdf`, { type: "application/pdf" });
      const fd = new FormData();
      fd.append("billPdf", file);
      fd.append("items", JSON.stringify(items));

      const res = await axios.post(`${API}/billing/sendBill/${id}`, fd, {
        headers: { "Content-Type": "multipart/form-data" }
      });
      console.log("sendBill response:", res.data);
      setMsg("Bill sent. URL: " + (res.data || ""));
    } catch(err){
      console.error("sendBill failed:", err);
      setMsg("Failed to send bill (see console)");
    } finally {
      setLoading(false);
    }
  };

  if(loading) return <div className="billing-page"><div className="billing-card">Loading...</div></div>;

  // If no data (e.g., backend returned 404), show helpful message
  if(!data) return <div className="billing-page"><div className="billing-card">{msg || "No data"}</div></div>;

  return (
    <div className="billing-page">
      <div className="billing-card">
        <h2>Billing (Bill #{id})</h2>

        <div className="customer-block">
          <div>
            <strong>Customer:</strong> {data.customerName || data.customer?.name || data.name || "N/A"}
          </div>
          <div style={{color:"#666"}}>
            {data.customerEmail || data.customer?.email || ""}
          </div>
        </div>

        <div className="items-section">
          <h3>Items</h3>
          {items.map((it, idx) => (
            <div className="item-row" key={idx}>
              <input className="item-input name" value={it.name} onChange={e => handleItemChange(idx, "name", e.target.value)} placeholder="Particular" />
              <input className="item-input qty" type="number" value={it.qty} onChange={e => handleItemChange(idx, "qty", e.target.value)} />
              <input className="item-input price" type="number" value={it.price} onChange={e => handleItemChange(idx, "price", e.target.value)} />
              <div className="item-total">₹{((it.qty||0)*(it.price||0)).toFixed(2)}</div>
              <button className="remove-btn" onClick={() => removeItem(idx)}>✖</button>
            </div>
          ))}

          <button className="add-btn" onClick={addItem}>+ Add item</button>
        </div>

        <div className="total-row">
          <div><strong>Total:</strong> ₹{total.toFixed(2)}</div>
          <div className="actions">
            <button className="send-btn" onClick={sendBill} disabled={loading}>{loading ? "Sending..." : "Send Final Bill"}</button>
            <button className="send-btn" style={{background:"#777", marginLeft:8}} onClick={() => nav("/bills")}>Back</button>
          </div>
        </div>

        {msg && <div className="message">{msg}</div>}
      </div>
    </div>
  );
}
