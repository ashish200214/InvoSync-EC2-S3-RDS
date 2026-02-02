import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import "./index.css";
import App from "./App";
import QuotationList from "./components/QuotationList";
import QuotationForm from "./components/QuotationForm";
import SendQuotation from "./components/SendQuotation";
import BillingList from "./components/BillingList";
import BillingPage from "./components/BillingPage";
import CustomerFiles from "./components/CustomerFiles";   // ⭐ IMPORT THIS

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<App />} >
          <Route index element={<Navigate to="/quotations" replace />} />
          {/* //replace -> dont add the entry to broswer history.
          // means when we go to quotations and press the back button it wont go back because replace will replace it instead of adding new history.
           */}
          <Route path="quotations" element={<QuotationList />} />
          <Route path="quotations/new" element={<QuotationForm />} />
          <Route path="quotations/:id/send" element={<SendQuotation />} />
          <Route path="bills" element={<BillingList />} />
          <Route path="bills/:id" element={<BillingPage />} />

          <Route path="customer-files" element={<CustomerFiles />} />
        </Route>
      </Routes>
    </BrowserRouter>
  </StrictMode>
);
