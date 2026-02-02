// src/components/CustomerFiles.jsx
import React, { useState } from "react";
import axios from "axios";
import Header from "./Header";
import Footer from "./Footer";
import "./CustomerFiles.css";

export default function CustomerFiles() {
  const [mobile, setMobile] = useState("");
  const [files, setFiles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [preview, setPreview] = useState(null); // { url, name, type }
  const API = import.meta.env.VITE_API_URL;

  const fetchFiles = async () => {
    setMessage("");
    setFiles([]);
    if (!mobile.trim()) {
      setMessage("Please enter a mobile number.");
      return;
    }

    setLoading(true);
    try {
      const res = await axios.get(
        `${API}/s3/files/${encodeURIComponent(mobile.trim())}?presign=true&expiry=600`
      );

      const raw = res.data || [];

      const mapped = raw.map((f) => {
        const name = f.fileName || (f.key ? f.key.split("/").pop() : "unknown");
        // normalize lastModified
        let lm = null;
        if (f.lastModified) lm = new Date(f.lastModified);
        else if (f.lastModifiedEpoch) lm = new Date(f.lastModifiedEpoch);

        const ext = (name.split(".").pop() || "").toLowerCase();
        const type = fileTypeFromExt(ext);

        return {
          key: f.key,
          name,
          url: f.url,
          lastModified: lm,
          size: f.size || 0,
          ext,
          type
        };
      });

      // sort latest first when date available
      mapped.sort((a, b) => {
        if (a.lastModified && b.lastModified) return b.lastModified - a.lastModified;
        if (a.lastModified) return -1;
        if (b.lastModified) return 1;
        return a.name.localeCompare(b.name);
      });

      setFiles(mapped);
      if (mapped.length === 0) setMessage("No files found for this customer.");
    } catch (err) {
      console.error(err);
      setMessage("Failed to fetch files. Check console for details.");
    } finally {
      setLoading(false);
    }
  };

  // derive file type
  function fileTypeFromExt(ext) {
    if (!ext) return "other";
    if (["png", "jpg", "jpeg", "gif", "webp", "bmp"].includes(ext)) return "image";
    if (ext === "pdf") return "pdf";
    if (ext === "cdr") return "cdr";
    if (["dwg", "dxf", "cdr", "svg"].includes(ext)) return "drawing";
    if (["txt", "doc", "docx", "xls", "xlsx", "ppt", "pptx"].includes(ext)) return "document";
    return "other";
  }

  const formatDate = (d) => (d ? d.toLocaleString() : "Unknown date");

  return (
    <div className="customer-files-page">
      <Header />

      <div className="content-container">
        <h2 className="page-title">Customer S3 Files</h2>

        <div className="search-row">
          <input
            className="search-input"
            placeholder="Enter customer mobile number"
            value={mobile}
            onChange={(e) => setMobile(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && fetchFiles()}
          />
          <button className="search-btn" onClick={fetchFiles} disabled={loading}>
            {loading ? "Loading..." : "Show Files"}
          </button>
        </div>

        {message && <div className="message">{message}</div>}

        <div className="files-grid">
          {files.map((f, i) => (
            <div className="file-card" key={f.key || `${f.name}-${i}`}>
              {/* IMAGE - large preview */}
              {f.type === "image" ? (
                <>
                  <img
                    src={f.url}
                    className="file-img-large"
                    alt={f.name}
                    onClick={() => setPreview({ url: f.url, name: f.name, type: f.type })}
                    loading="lazy"
                  />
                  <div className="file-name" title={f.name}>{f.name}</div>
                  <div className="file-date">{formatDate(f.lastModified)}</div>
                  <div className="card-actions">
                    <button className="file-btn" onClick={() => setPreview({ url: f.url, name: f.name, type: f.type })}>
                      Preview
                    </button>
                    <a className="file-btn secondary" href={f.url} target="_blank" rel="noreferrer">Download</a>
                  </div>
                </>
              ) : f.type === "pdf" ? (
                // PDF - pdf icon + view in new tab
                <>
                  <div className="file-icon-block pdf">
                    <svg width="46" height="56" viewBox="0 0 46 56" xmlns="http://www.w3.org/2000/svg">
                      <rect width="46" height="56" rx="6" fill="#E53935"/>
                      <text x="23" y="33" textAnchor="middle" fill="#fff" fontSize="14" fontWeight="700">PDF</text>
                    </svg>
                  </div>
                  <div className="file-name" title={f.name}>{f.name}</div>
                  <div className="file-date">{formatDate(f.lastModified)}</div>
                  <div className="card-actions">
                    <a className="file-btn" href={f.url} target="_blank" rel="noreferrer">View / Download</a>
                  </div>
                </>
              ) : f.type === "cdr" ? (
                // CDR / drawing special icon
                <>
                  <div className="file-icon-block cdr">
                    <svg width="46" height="46" viewBox="0 0 46 46" xmlns="http://www.w3.org/2000/svg">
                      <rect width="46" height="46" rx="8" fill="#0ea5a4"/>
                      <text x="23" y="29" textAnchor="middle" fill="#fff" fontSize="14" fontWeight="700">CDR</text>
                    </svg>
                  </div>
                  <div className="file-name" title={f.name}>{f.name}</div>
                  <div className="file-date">{formatDate(f.lastModified)}</div>
                  <div className="card-actions">
                    <a className="file-btn" href={f.url} target="_blank" rel="noreferrer">Download</a>
                  </div>
                </>
              ) : (
                // other types
                <>
                  <div className="file-icon-block other">
                    <svg width="46" height="56" viewBox="0 0 46 56" xmlns="http://www.w3.org/2000/svg">
                      <rect width="46" height="56" rx="6" fill="#64748b"/>
                      <text x="23" y="33" textAnchor="middle" fill="#fff" fontSize="12" fontWeight="700">DOC</text>
                    </svg>
                  </div>
                  <div className="file-name" title={f.name}>{f.name}</div>
                  <div className="file-date">{formatDate(f.lastModified)}</div>
                  <div className="card-actions">
                    <a className="file-btn" href={f.url} target="_blank" rel="noreferrer">View / Download</a>
                  </div>
                </>
              )}
            </div>
          ))}
        </div>
      </div>

      <Footer />

      {/* modal preview (large for images) */}
      {preview && (
        <div className="modal" onClick={() => setPreview(null)}>
          <div className="modal-box" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <strong>{preview.name}</strong>
              <button className="close-btn" onClick={() => setPreview(null)}>Close</button>
            </div>
            {preview.type === "image" ? (
              <img src={preview.url} alt={preview.name} className="modal-img-large" />
            ) : (
              <a href={preview.url} target="_blank" rel="noreferrer" className="file-btn">Open</a>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
