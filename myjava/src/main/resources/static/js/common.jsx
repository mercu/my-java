function ScrollLayer(props) {
    const outerId = props.outerId;
    const innerId = props.innerId;
    return (
        <div className={"panel panel-default"} id={"floatMenu"} style={{position:"fixed", bottom:"30px", right:"20px"}}>
            <div className={"panel-body"}>
                <button className={'btn btn-block btn-default'} onClick={(e) => {$(outerId).scrollTop(0); e.preventDefault()}}>TOP</button>
                <button className={'btn btn-block btn-default'} onClick={(e) => {$(outerId).scrollTop($(innerId).height()); e.preventDefault()}}>BTM</button>
            </div>
        </div>
    );
}

