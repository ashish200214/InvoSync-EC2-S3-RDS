import { Link } from "react-router-dom";
import "./Header.css";

export default function Header() {
  return (
    <header className="main-header">
      <div className="logo">InfoSync</div>

      <nav>
        <Link to="/">Home</Link>
        <Link to="/quotations">Quotations</Link>
        <Link to="/quotations/new">New Quotation</Link>
        <Link to="/bills">Bills</Link>
      </nav>
    </header>
  );
}
