/* axios 라이브러리 이용  async/await 이용하면 비동기 처리를 동기화된 코드처럼 작성 가능
    async : 비동기 처리를 위한 함수 명시
    await : async 함수 내에서 비동기 호출하는 부분에 사용
*/

/*
비동기 통신을 하는 부분, reply.js는 비동기통신(호출)만 하고 Promise객체를 렌더링하는쪽에 넘김
결과가 오면 너가 알아서 처리해 이런 느낌
 */

async function get1(bno) {
    const result = await axios.get(`/replies/list/${bno}`); //미리 정의를 해놓고

    // console.log(result);
    return result;
}

// goLast : 마지막 댓글 페이지 호출되도록 하는 변수
// 마지막 댓글 페이지가 열리도록 할 때 size(출력 데이터 수)와 total(총 데이터 수)를 가지고 페이지 계산해서 리턴
async function getList({bno, page, size, goLast}){

    const result = await axios.get(`/replies/list/${bno}`,{params : {page, size}});

    if(goLast) {
        const total = result.data.total;
        const lastPage = parseInt(Math.ceil(total/size));
        console.log(lastPage);
        return getList({bno:bno, page:lastPage, size:size});
    }

    return result.data;
}

async function addReply(replyObj) {
    const response = await axios.post('/replies/',replyObj);  //getList()랑 달리 param이 한개라서 생략한듯
    return response.data;
}

//댓글 조회 (get)
async function getReply(rno) {
    const response = await axios.get(`/replies/${rno}`);
    return response.data;
}

//댓글 수정 (put)
async function modifyReply(replyObj) {
    const response = await axios.put(`/replies/${replyObj.rno}`,replyObj); //2번쨰 인자는 param 바디로 전송인듯
    return response.data;
}

//댓글 삭제 기능 delete 요청
async function removeReply(rno) {
    const response = await axios.delete(`/replies/${rno}`);
    return response.data;
}

