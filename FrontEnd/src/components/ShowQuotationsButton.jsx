import React from "react";
import { useNavigate } from "react-router-dom";

const ShowQuotationsButton = () => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate("/quotations"); // दुसऱ्या page वर navigate
  };

  return (
    <div style={{ textAlign: "center", marginTop: "30px" }}>
      <button
        onClick={handleClick}
        style={{
          padding: "10px 20px",
          backgroundColor: "#007bff",
          color: "white",
          border: "none",
          borderRadius: "8px",
          cursor: "pointer",
          fontSize: "16px",
        }}
      >
        Show All Quotations
      </button>
    </div>
  );
};

export default ShowQuotationsButton;
