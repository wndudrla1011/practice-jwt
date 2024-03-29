import Button from 'react-bootstrap/Button';
import Container from 'react-bootstrap/Container';
import Form from 'react-bootstrap/Form';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import React, { useContext } from 'react';
import { Link } from 'react-router-dom';
import { LoginContext } from '../contexts/LoginContextProvider';
import styles from './Header.module.css';

function Header() {
  const { isLogin, logout } = useContext(LoginContext);

  return (
    <div>
      <Navbar expand="lg" className="bg-body-tertiary">
        <Container fluid>
          <Navbar.Brand href="#">Rootable's Library</Navbar.Brand>
          <Navbar.Toggle aria-controls="navbarScroll" />
          <Navbar.Collapse id="navbarScroll">
            <Nav
              className="me-auto my-2 my-lg-0"
              style={{ maxHeight: '100px' }}
              navbarScroll
            >
              <Link to="/" className="nav-link">
                홈
              </Link>
              <Link to="/posts.add" className="nav-link">
                글쓰기
              </Link>
              <NavDropdown title="활동 내역" id="navbarScrollingDropdown">
                <NavDropdown.Item href="#action3">나의 게시물</NavDropdown.Item>
                <NavDropdown.Item href="#action4">나의 댓글</NavDropdown.Item>
              </NavDropdown>
              {!isLogin ? (
                <ul>
                  <li>
                    <Link to="/members/add" className="nav-link">
                      회원가입
                    </Link>
                  </li>
                  <li>
                    <Link to="/login" className="nav-link">
                      로그인
                    </Link>
                  </li>
                </ul>
              ) : (
                <ul>
                  <li>
                    <Link to="/members/:memberId" className="nav-link">
                      계정 관리
                    </Link>
                  </li>
                  <li>
                    <button className="nav-link" onClick={() => logout()}>
                      로그아웃
                    </button>
                  </li>
                </ul>
              )}
            </Nav>
            <Form className="d-flex">
              <Form.Control
                type="search"
                placeholder="Search"
                className="me-2"
                aria-label="Search"
              />
              <Button variant="outline-success">Search</Button>
            </Form>
          </Navbar.Collapse>
        </Container>
      </Navbar>
    </div>
  );
}

export default Header;
