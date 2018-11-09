var navigatorDOM = null;
function showNavigator(e) {
    if (typeof e != "undefined") e.preventDefault();

    if (navigatorDOM == null) {
        ReactDOM.render(
            <Navigator/>
            , document.getElementById("navi")
        );
    } else {
        // partCategoriesAjax(parentId);
    }

}

class Navigator extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            // parentId : props.parentId,
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        navigatorDOM = this;
    }

    componentWillUnmount() {
        navigatorDOM = null;
    }

    render() {
        return (
            <nav className="navbar navbar-default">
                <div className="container-fluid">
                    <div className="navbar-header">
                        <a className="navbar-brand" href="#">MercuBricks!</a>

                    </div>
                    <ul className="nav navbar-nav">
                        <li className="active"><a href="#">Home</a></li>
                        <li><a href="#" onClick={(e) => {partCategories();e.preventDefault();}}>Part Categories</a></li>
                        <Login loginUser={loginUser}/>
                    </ul>
                </div>
            </nav>
        );
    }
}

function Login(props) {
    if (props.loginUser != null) {
        return <li><a href="/logout">Welcome, {props.loginUser}!<br/>Logout</a></li>;
    } else {
        return <li><a href="/login">Login</a></li>;
    }
}


showNavigator();
