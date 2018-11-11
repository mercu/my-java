var loginUser = undefined;
var loginUserId = undefined;
var loginUserAdmin = undefined;

function showNavigator(loginUser, loginUserId, loginUserAdmin, e) {
    if (typeof e != "undefined") e.preventDefault();

    if (navigatorDOM == null) {
        ReactDOM.render(
            <Navigator loginUser={loginUser} loginUserId={loginUserId} loginUserAdmin={loginUserAdmin}/>
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
            loginUser : props.loginUser,
            loginUserId : props.loginUserId,
            loginUserAdmin : props.loginUserAdmin
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
                        <button type="button" className="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                            <span className="icon-bar"></span>
                        </button>
                        <a className="navbar-brand" href="#">MercuBricks!</a>
                    </div>
                    <div className="collapse navbar-collapse" id="myNavbar">
                        <ul className="nav navbar-nav">
                            <li className="active"><a href="#">Home</a></li>
                            <li><a href="#" onClick={(e) => {hideAll();partCategories();e.preventDefault();}}>Part Categories</a></li>
                            <li><a className={(this.state.loginUserAdmin == true ? '' : ' hide')} href="#" onClick={(e) => {hideAll();myParts();e.preventDefault();}}>My Parts</a></li>
                        </ul>
                        <ul className="nav navbar-nav navbar-right">
                            <li><a href="#">{this.state.loginUser !== undefined ? "Welcome, " + this.state.loginUser + "!" : ""}</a></li>
                            <Login loginUser={this.state.loginUser}/>
                        </ul>
                    </div>
                </div>
            </nav>
        );
    }
}

function hideAll() {
    $("#partCategories").addClass("hide");
    $("#myParts").addClass("hide");

}

function Login(props) {
    if (props.loginUser !== undefined) {
        return <li><a href="/logout">Logout</a></li>;
    } else {
        return <li><a href="/login">Login</a></li>;
    }
}

var navigatorDOM = null;
// login 체크
function checkLogin() {
    $.ajax({
        cache : false,
        url:"/loginUser",
        type : "GET",
        dataType : "json",
        contentType: "application/json;charset=UTF-8",
        async : true
    }).always(function(data) {
        if (data.userId == "anonymousUser") {
            navigatorDOM.setState({
                loginUser : undefined,
                loginUserId : undefined,
                loginUserAdmin : undefined
            });
        } else {
            navigatorDOM.setState({
                loginUser : data.nick,
                loginUserId : data.userId,
                loginUserAdmin : data.isAdmin
            });
        }
    });
}



showNavigator();
checkLogin();



