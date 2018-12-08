var candidateDOM = null;
function candidate(e) {
    if (typeof e != "undefined") e.preventDefault();

    if (candidateDOM == null) {
        ReactDOM.render(
            <MyParts/>
            , document.getElementById("candidate")
        );
    } else {
        candidateAjax();
    }
    $("#candidate").removeClass("hide");

}

class MyParts extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            items : []
        };
    }

    setState(state) {
        super.setState(state);
    }

    componentDidMount() {
        candidateDOM = this;
        candidateAjax(this.state.parentId);
    }

    componentWillUnmount() {
        candidateDOM = null;
    }

    render() {
        return (
            <CandidateRoot
                items={this.state.items}
            />
        );
    }
}

function CandidateRoot(props) {
    return (
        <div className={'panel panel-default'}>
            <div className={'panel-body'}>
                <table className="table table-bordered">
                    <thead>
                    <tr>
                        <th>matchId</th>
                    </tr>
                    </thead>
                    <tbody>
                    {props.items.map(function(item, key) {
                        return <tr key={key}>
                            <td>{item}</td>
                        </tr>;
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function candidateAjax() {
    $.ajax({
        url:"/admin/candidate",
        type : "GET",
        dataType : "json",
        data : {},
        contentType: "application/json;charset=UTF-8",
        async : true
    }).done(function(data) {
        candidateDOM.setState({
            items : data
        });
    });
}

